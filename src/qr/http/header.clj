(ns qr.http.header)

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