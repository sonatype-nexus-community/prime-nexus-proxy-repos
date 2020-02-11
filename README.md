# Prime Proxy Repository(ies) on Hosted Repo Publish
> Use when teams want to automatically keep a one or many proxy repositories primed with the lastest uploads from the remote hosted repository.

## Overview
A repository script is used as a webhook receiver for the webhook capability applied to the hosted repository.  After upload of a component, the script queries the hosted repository for the component.  The list of component assets is returned.  The script then makes calls for each asset to the proxy repository.  The proxy repository loads the requested assets from the hosted repository.  The two repositories are now in "sync"

## Components
#### On Hosted Repo Instance
* `provision-primeMultiProxyOnRemotePublish.sh` - Formats and sends the groovy script to the repository server.
* `primeMultiProxyOnRemotePublish.groovy` - Hosted repository script that handles the webhook call from the hosted repository.
* `primeRetry.groovy` - Task that runs on an interval to look for any priming failures and retries them.
#### On Proxy Repo Instance
* `provision-primeFromTheProxy.sh` - Formats and sends the groovy script to the repository server.
* `primeFromTheProxy.groovy` - Proxy repository script that makes request locally for the asset and pulls from the hosted repository.

## Getting Started
* In `provision-primeMultiProxyOnRemotePublish.sh` and `provision-primeFromTheProxy.sh` edit username, password, and host
* In `primeMultiProxyOnRemotePublish.groovy` edit the PROXY_URL_PORT array with a list of proxy server you wish to prime.
```
PROXY_URL_PORT = [
     "http://host.docker.internal:8077/service/rest/v1/script/primeFromTheProxy/run",
     "http://host.docker.internal:8078/service/rest/v1/script/primeFromTheProxy/run"
   ]
   ```
* In `primeFromTheProxy.groovy` set the BASE_URL and AUTH.  BASE_URL should be localhost and the port the server is running on.  AUTH is for authentication if needed.
```
BASE_URL = "http://localhost:8081"
AUTH = 'Basic YWRtaW46YWRtaW4xMjM='
```
* Run provision scripts to upload the groovy script.  You will run a provision script for each hosted and proxy server.
* Create a anon-script-runner role with `nx-script-*-run` privilege and assign it to the anonymous user.  
For finer control, replace the `*` with the script name.  Why anonymous? The webhook assume that it is sending to a server without authentication enabled.
  
* Set a webhook capability for the hosted repository. 
  * URL: `http://localhost:8081/service/rest/v1/script/primeMultiProxyOnRemotePublish/run`
  * Event Type: `asset`

* To enable retrying, create a script task on the interval you wish to retry.  Enter the contents of `primeRetry.groovy` into the source field for the script task.

## TO DO and Wish List
* Provide UI to set all variables
* More in-depth testing

## Limitations
* Assumes the proxy and the hosted repository names are the same
* Primes only on create