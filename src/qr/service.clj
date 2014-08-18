(ns qr.service
    (:require [io.pedestal.http :as bootstrap]
              [io.pedestal.http.route :as route]
              [io.pedestal.http.body-params :as body-params]
              [io.pedestal.http.route.definition :refer [defroutes]]
              [ring.util.response :as ring-resp]
              [clojure.data.json :as json]
              [qr.http.header :as qualis-header]))

(defn about-page
  "Serve about page"
  [request]
  (ring-resp/response (format "Clojure %s - served from %s"
                              (clojure-version)
                              (route/url-for ::about-page))))

(defn top-level
  "Serve top-level request"
  [request]
  (let [[linkHeader LinkHeaderValue]
      (qualis-header/get-link-header "123" "text/plain" "GET URL" "GET")]
    (ring-resp/header
      (ring-resp/response (if (= "image/png" (get (:headers request) "accept"))
          "the png" "the url"))
        linkHeader LinkHeaderValue)))

(defn top-level-post
  "Satisfy top-level post request"
  [request]
  (let [[linkHeader LinkHeaderValue]
      (qualis-header/get-link-header "123" "image/png" "GET PNG" "GET")]
    (ring-resp/header
      (ring-resp/response "")
        linkHeader LinkHeaderValue)))

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
