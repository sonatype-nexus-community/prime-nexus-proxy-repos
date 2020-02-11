import groovy.json.*

BUFFER_SIZE = 4096
RETRY_DIR = '/tmp/retryDir'
PROXY_URL_PORT = [
 "http://host.docker.internal:8077/service/rest/v1/script/primeFromTheProxy/run",
  // "http://host.docker.internal:8078/service/rest/v1/script/primeFromTheProxy/run"
]

def payload = new JsonSlurper().parseText(args)

if (payload.action == "CREATED" && payload.asset.name != '') {
  path = '{"path": "/repository/' + payload.repositoryName + '/' + payload.asset.name + '"}'
  log.info("PATH: " + path)
  sendToProxies(path)

} else {
  log.info("SKIPPING ACTION: " + payload.action )
}

return

def sendToProxies(asset_url_path) {
    sleep(3000)
  // For each proxy repo we want to prime
  PROXY_URL_PORT.each {
    try {
        proxyURL = "${it}"
        full_asset_url = proxyURL + asset_url_path
        log.info('Priming : ' + full_asset_url)
        def nexusProxy = new URL(proxyURL).openConnection() as HttpURLConnection
        nexusProxy.setRequestMethod("POST")
        nexusProxy.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        nexusProxy.setDoOutput(true)
        nexusProxy.setRequestProperty("Accept", "application/json")

        OutputStream os = nexusProxy.getOutputStream()
        os.write(asset_url_path.getBytes("UTF-8"))
        os.close()

        log.info("Response: " + nexusProxy.responseMessage)

      } catch (Exception e) {
            File directory = new File(RETRY_DIR)
            if (! directory.exists()) {
               directory.mkdirs()
            }
            log.info("urlPath before replaceAll" + proxyURL)
            retryFile = proxyURL + "::" + asset_url_path
            fileName = retryFile.replaceAll("[^a-zA-Z0-9\\.\\-]", "_")
            log.info("Setting fileName: " + fileName)

            File urlFile = new File(RETRY_DIR + "/" + fileName)
            urlFile.write retryFile
              log.info(e.toString())
              log.info("ERROR: Unable to process asset.  Storing for retry.")
      }
  }
  
}


