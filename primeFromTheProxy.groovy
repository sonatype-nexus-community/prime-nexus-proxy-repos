import groovy.json.JsonSlurper

BASE_URL = "http://localhost:8081"
AUTH = 'Basic YWRtaW46YWRtaW4xMjM='

BUFFER_SIZE = 4096

def payload = new JsonSlurper().parseText(args)
log.info("Receiving for prime: " + payload)

requestFromProxy(payload.path)

def requestFromProxy(path) {
  try {
    proxyURL = BASE_URL + path
    log.info('Priming: ' + proxyURL)
    def nexusProxy = new URL(proxyURL).openConnection() as HttpURLConnection
    nexusProxy.setRequestProperty('Authorization', AUTH)
    if ( nexusProxy.responseCode == HttpURLConnection.HTTP_OK ) {
        InputStream inputStream = nexusProxy.getInputStream()
        String dev_null = '/dev/null'

        FileOutputStream outputStream = new FileOutputStream(dev_null)

        byte[] buffer = new byte[BUFFER_SIZE]
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer,0, bytesRead)
        }

        outputStream.close()
        inputStream.close()
    } else {
      log.info("ERROR: NOT HttpURLConnection.HTTP_OK  " + nexusProxy.responseCode + ": " + nexusProxy.inputStream.text)
    }
  } catch (Exception e) {
            log.info("ERROR: Unable to process asset." + e.toString())
  }
}


