(ns qr.service
    (:require [io.pedestal.http :as bootstrap]
              [io.pedestal.http.route :as route]
              [io.pedestal.http.body-params :as body-params]
              [io.pedestal.http.route.definition :refer [defroutes]]
              [ring.util.response :as ring-resp]
              [clojure.data.json :as json]))

(defn about-page
  "Serve about page"
  [request]
  (ring-resp/response (format "Clojure %s - served from %s"
                              (clojure-version)
                              (route/url-for ::about-page))))

(defn top-level
  "Serve top-level request"
  [request]
  (ring-resp/response "Hello World!"))

(defn top-level-post
  "Satisfy top-level post request"
  [request]
  (ring-resp/header (ring-resp/response "http://www.google.com.au/")
    "Link" "</123>; rel=\"self\"; type=\"image/png\"; title=\"GET PNG\"; method=\"GET\""))

(defroutes routes
  [[["/" {:get top-level :post top-level-post}
     ;; Set default interceptors for /about and any other paths under /
     ^:interceptors [(body-params/body-params) bootstrap/html-body]
     ["/about" {:get about-page}]]]])

;; Consumed by qr.server/create-server
;; See bootstrap/default-interceptors for additional options you can configure
(def service "Service definition" {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; :bootstrap/interceptors []
              ::bootstrap/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::bootstrap/allowed-origins ["scheme://host:port"]

              ;; Root for resource interceptor that is available by default.
              ::bootstrap/resource-path "/public"

              ;; Either :jetty or :tomcat (see comments in project.clj
              ;; to enable Tomcat)
              ;;::bootstrap/host "localhost"
              ::bootstrap/type :jetty
              ::bootstrap/port 8080})
