# Test Stub Corda 3.0 Network Map
A quick simple HTTP-based Corda network map for testing (not meant for production)

## Running
```
gradle run
```

## Building
```
gradle distZip # (or gradle distTar)
```

`build/distributions` will contain a zip file with all dependencies + OS-specific
scripts for running

The server can then be run with:
```
bin/stub-corda-networkmap server <path to yaml config>
```

## Configuration
`expiration` (in seconds) in the yaml config sets the expiration / cache HTTP header
that's then used by Corda nodes how often they'd query the network map.

Additional server properties can be configured using the standard Dropwizard configuration: http://www.dropwizard.io/1.3.0/docs/manual/configuration.html#http

## Connecting nodes: adding notary
Each node that will have `OU=Notary` in its legal name will be automatically added 
to this test stub network map as a notary. Adding notary nodes changes the network map
and normally would require signalling an upgrade, and having node operators manually 
approve the network map update (via RPC / node shell).

For testing, the temporary procedure is:

1. run notary nodes with `OU=Notary` in their legal names and compatibilityZoneURL in their
config (e.g. `compatibilityZoneURL: "http://localhost:8080"`).

2. notary nodes will self-shutdown with an exception: `network.NetworkMapUpdater.updateNetworkMapCache - Node is using parameters with hash: ... but network map is advertising: ...`

3. delete `network-parameters` in the notary nodes' directories and run them again

4. run all other nodes with the compatibilityZoneURL in their configs
