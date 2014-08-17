(ns qr.http.header)

(defn get-link-header
  "get link header string method"
  [id type title method]
  ["Link" (format "</%s>; rel=\"self\"; type=\"%s\"; title=\"%s\"; method=\"%s\"" id type title method)])
