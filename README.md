# Vision

The current implementation is a little bit "shortsighted". Instead we need to build 2 separate components:
* reserve monitoring (monitors reserve transactions and balance and notifies slack)
* lhv gateway implementation (monitors gateway transactions, makes kryptoeuro transactions for deposits and real euro transactions for withdrawals)

# bank-gateway

Bank gateway handles communication between the reserve (kept in LHV) and the cryptocurrency supply. 

This includes
* updating total reserve amount from uploaded statement files
* parsing transactions from uploaded statement files
* live integration with LHV API
* (TODO) putting cryptocurrency on people's accounts from gateway account
* (TODO) handling crypto->fiat payouts

## production environment

Default location for LHV keystore is `lhv.jks` in working directory.
Default location for reserve bank Ethereum private key is `.reserve.key` in home folder.

The config params / system properties for overriding default locations are, respectively:
* `lhv.connect.keyStore.path`
* `reserveBank.ethereum.keyFile`

Gateway is accessible at https://teller.euro2.ee

