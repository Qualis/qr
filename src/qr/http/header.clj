(ns qr.http.header)

(def ^:const TEXT_HTML_MIME_TYPE "text/html;charset=UTF-8")
(def ^:const IMAGE_PNG_MIME_TYPE "image/png")
(def ^:const PLAIN_TEXT_MIME_TYPE "text/plain")
(def ^:const FORM_MIME_TYPE "application/x-www-form-urlencoded")
(def ^:const APPLICATION_JSON_MIME_TYPE "application/json")
(def ^:const ACCEPT_IMAGE_PNG {"Accept" IMAGE_PNG_MIME_TYPE})
(def ^:const ACCEPT_PLAIN_TEXT {"Accept" PLAIN_TEXT_MIME_TYPE})
(def ^:const ACCEPT_HTML_TEXT {"Accept" TEXT_HTML_MIME_TYPE})
(def ^:const TEXT_HTML_CONTENT_TYPE {"Content-Type" TEXT_HTML_MIME_TYPE})
(def ^:const TEXT_PLAIN_CONTENT_TYPE {"Content-Type" PLAIN_TEXT_MIME_TYPE})
(def ^:const IMAGE_PNG_CONTENT_TYPE {"Content-Type" IMAGE_PNG_MIME_TYPE})
(def ^:const JSON_CONTENT_TYPE {"Content-Type" APPLICATION_JSON_MIME_TYPE})
(def ^:const FORM_CONTENT_TYPE {"Content-Type" FORM_MIME_TYPE})

(defn get-link-header
  [id type title method]
  ["Link"
    (format "</%s>;rel=\"self\";type=\"%s\";title=\"%s\";method=\"%s\""
      id type title method)])

(defn get-png-link-header
  [id]
  (get-link-header id "image/png" "GET PNG" "GET"))

(defn get-url-link-header
  [id]
  (get-link-header id "text/plain" "GET URL" "GET"))

(defn get-id-from-link-header
  [response]
  (second (re-find #"<\/(.*?)>"
    (get (:headers response) "Link"))))
