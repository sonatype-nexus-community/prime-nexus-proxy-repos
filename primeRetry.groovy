def BUFFER_SIZE = 4096
def RETRY_DIR = new File('/tmp/retryDir')

if (RETRY_DIR.listFiles().size() <= 0) {
    log.info ("primeRetry: No retry files found. Exiting.")
    return
}

RETRY_DIR.eachFile {

    retryInfo = it.getText('UTF-8').split("::")
    proxyURL = retryInfo[0]
    asset_url_path = retryInfo[1]

     try {
         full_asset_url = proxyURL + asset_url_path
         log.info('Retry Priming : ' + full_asset_url)

         def nexusProxy = new URL(proxyURL).openConnection() as HttpURLConnection
         nexusProxy.setRequestMethod("POST")
         nexusProxy.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
         nexusProxy.setDoOutput(true)
         nexusProxy.setRequestProperty("Accept", "application/json")

         OutputStream os = nexusProxy.getOutputStream()
         os.write(asset_url_path.getBytes("UTF-8"))
         os.close()

         log.info('Removing asset retry file: ' + it)
         it.delete()

    } catch (Exception e) {
        log.info(e.toString())
        log.info("ERROR: Enable to retry asset: " + it + " Will retry again.")
  }
}