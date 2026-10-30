# switcloud-l3-template-kt
Switcloud L3 template Kotlin application

This app demonstrates how to perfom a single payment transaction using [Switcloud](https://docs.switstack.io/switcloud/) with Switcloud client SDK.

The app uses a **POI_ID** and **POI_CONFIG_ID** (already registered in the selected switcloud environement) with a hardcoded Transaction Related Data to create a payment with Switcloud API SDK.

Once authenticated against Switcloud, for the basic usage, a payment transaction is initiated using these calls : 
```
initialize()
configure()
initiate()
if( OPS.status == ONLINE_REQUEST) {
    complete()
}
emitReceipt()
```

It is also possible to get a deeper control to manage more precisely the steps, in this case you can use the advanced apis of **SwitcloudClt** :

[Link to switcloud-clt-kt documentation](https://docs.switstack.io/api/switcloud/clt/switcloud-clt-kt/)

## Configuration

Before building and running the app, add the following parameters to the **local.properties** file located in the project root directory:
```
SWITSTACK_CLIENT_ATTESTATION_SECRET="secret-provided-by-switstack"

LOCAL_SWITCLOUD_URL="http://url-provided-by-switstack"
LOCAL_SWITCLOUD_CLIENT_ID="id-provided-by-switstack"
LOCAL_SWITCLOUD_CLIENT_SECRET="secret-provided-by-switstack"
LOCAL_POI_ID="poi-id-provided-by-switstack"
LOCAL_POI_CONFIG_ID="poi-config-id-provided-by-switstack"
```

Once configured, these values are injected into the **Conf.kt** file. You can also use this file to configure the Transaction Related Data passed to the Switcloud client with the TRD variable (TLV format).