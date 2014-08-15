(ns qr.service-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [qr.service :as service]))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(deftest top-level-test
  (is (=
       (:body (response-for service :get "/"))
       "Hello World!"))
  (is (=
       (:headers (response-for service :get "/"))
       {"Content-Type" "text/html;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"})))

(deftest top-level-test
  (is (=
       (:body (response-for service :post "/"
               :params {:url "http://www.google.com.au/"}))
       "http://www.google.com.au/"))
  (is (=
       (:headers (response-for service :post "/"
               :params {:url "http://www.google.com.au/"}))
       {"Content-Type" "text/html;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"
        "Link" "</123>; rel=\"self\"; type=\"image/png\"; title=\"GET PNG\"; method=\"GET\""})))

(deftest about-page-test
  (is (.contains
       (:body (response-for service :get "/about"))
       "Clojure 1.6"))
  (is (=
       (:headers (response-for service :get "/about"))
       {"Content-Type" "text/html;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"})))
