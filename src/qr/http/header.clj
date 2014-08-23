(ns qr.http.header)

(def ^:const TEXT_HTML_CONTENT_TYPE {"Content-Type" "text/html;charset=UTF-8"})
(def ^:const TEXT_PLAIN_CONTENT_TYPE {"Content-Type" "text/plain"})
(def ^:const IMAGE_PNG_CONTENT_TYPE {"Content-Type" "image/png"})
(def ^:const ACCEPT_IMAGE_PNG {"Accept" "image/png"})
(def ^:const ACCEPT_PLAIN_TEXT {"Accept" "text/plain"})

(defn get-link-header
  "get link header string"
  [id type title method]
  ["Link"
    (format "</%s>;rel=\"self\";type=\"%s\";title=\"%s\";method=\"%s\""
      id type title method)])

(defn get-png-link-header
  "get standard PNG link header string"
  [id]
  (get-link-header id "image/png" "GET PNG" "GET"))

(defn get-url-link-header
  "get standard URL link header string"
  [id]
  (get-link-header id "text/plain" "GET URL" "GET"))

(defn get-id-from-link-header
  "gets the id from a response link header and sets last-generated-id"
  [response]
  (second (re-find #"<\/(.*?)>"
    (get (:headers response) "Link"))))
