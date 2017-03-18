package eu.cryptoeuro.bankgateway.services.transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.RandomStringUtils;

import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_060_001_03.AccountIdentification4Choice;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_060_001_03.AccountReportingRequestV03;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_060_001_03.CashAccount24;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_060_001_03.DatePeriodDetails1;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_060_001_03.Document;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_060_001_03.GroupHeader59;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_060_001_03.ObjectFactory;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_060_001_03.Party12Choice;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_060_001_03.PartyIdentification43;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_060_001_03.QueryType3Code;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_060_001_03.ReportingPeriod1;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_060_001_03.ReportingRequest3;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_060_001_03.TimePeriodDetails1;

/**
 * Util class containing functionality for creating LHV Connect Account Statement Request objects
 *
 * @author Erko Hansar
 * @author Kaarel Jõgeva
 */
@Slf4j
public class AccountStatementRequestUtil {

    private static final String CAMT_053_001_02 = "camt.053.001.02";
    private static final ObjectFactory FACTORY = new ObjectFactory();

    /** 2016-10-14 */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    /** 17:00:00+02:00 */
    public static final String TIME_FORMAT = "HH:mm:ssXXX";
    /** 2016-10-14T17:21:10 */
    private static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private AccountStatementRequestUtil() {
        // No new instances
    }

    public static JAXBElement<Document> createRequestDocument(String iban, Date fromDate, Date toDate) {
        Date fromDateStart = normalizeDate(fromDate, true);
        Date toDateEnd = normalizeDate(toDate, false);

        Document document = new Document();
        AccountReportingRequestV03 accountRequest = new AccountReportingRequestV03();
        accountRequest.setGrpHdr(getGroupHeader());
        accountRequest.getRptgReq().add(createReportingRequest(iban, fromDateStart, toDateEnd));
        document.setAcctRptgReq(accountRequest);

        return FACTORY.createDocument(document);
    }

    ///// PRIVATE METHODS /////

    private static Date normalizeDate(Date inputDate, boolean isLowerBound) {
        if (inputDate == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(inputDate);
        cal.set(Calendar.HOUR_OF_DAY, isLowerBound ? 0 : 23);
        cal.set(Calendar.MINUTE, isLowerBound ? 0 : 59);
        cal.set(Calendar.SECOND, isLowerBound ? 0 : 59);
        cal.set(Calendar.MILLISECOND, isLowerBound ? 0 : 999);

        return cal.getTime();
    }

    private static ReportingRequest3 createReportingRequest(String iban, Date fromDate, Date toDate) {
        ReportingRequest3 request = new ReportingRequest3();
        request.setReqdMsgNmId(CAMT_053_001_02);

        CashAccount24 account = new CashAccount24();
        AccountIdentification4Choice choice = new AccountIdentification4Choice();
        choice.setIBAN(iban);
        account.setId(choice);
        request.setAcct(account);

        Party12Choice accountOwner = new Party12Choice();
        accountOwner.setPty(new PartyIdentification43());
        request.setAcctOwnr(accountOwner);

        ReportingPeriod1 reportingPeriod = new ReportingPeriod1();
        reportingPeriod.setFrToDt(getDatePeriodDetails(fromDate, toDate));
        reportingPeriod.setFrToTm(getTimePeriodDetails(fromDate, toDate));
        reportingPeriod.setTp(QueryType3Code.ALLL);
        request.setRptgPrd(reportingPeriod);

        return request;
    }

    private static TimePeriodDetails1 getTimePeriodDetails(Date fromDate, Date toDate) {
        TimePeriodDetails1 fromToTime = new TimePeriodDetails1();
        fromToTime.setFrTm(getXmlGregorianCalendar(fromDate, TIME_FORMAT));
        fromToTime.setToTm(getXmlGregorianCalendar(toDate, TIME_FORMAT));
        return fromToTime;
    }

    private static DatePeriodDetails1 getDatePeriodDetails(Date fromDate, Date toDate) {
        DatePeriodDetails1 fromToDate = new DatePeriodDetails1();
        fromToDate.setFrDt(getXmlGregorianCalendar(fromDate, DATE_FORMAT));
        fromToDate.setToDt(getXmlGregorianCalendar(toDate, DATE_FORMAT));
        return fromToDate;
    }

    private static GroupHeader59 getGroupHeader() {
        GroupHeader59 header = new GroupHeader59();
        header.setMsgId(RandomStringUtils.randomAlphanumeric(35));
        header.setCreDtTm(getXmlGregorianCalendar(new Date(), DATETIME_FORMAT));
        return header;
    }

    private static XMLGregorianCalendar getXmlGregorianCalendar(Date date, String format) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(new SimpleDateFormat(format).format(date));
        } catch (DatatypeConfigurationException e) {
            String message = "Failed to convert date to XmlGregorianCalendar";
            log.error(message, e);

            throw new RuntimeException(message, e);
        }
    }

}
