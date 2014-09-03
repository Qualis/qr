(ns qr.http.request)

(defn get-host-url
  [request id]
  (str
     (-> request :scheme name)
     "://"
     (get-in request [:headers "host"])
     "/"
     id))
