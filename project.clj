(defproject qr "0.0.1-SNAPSHOT"
  :description "URL.qual.is - URL shortening service"
  :url "http://qr.qual.is/"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
    [io.pedestal/pedestal.service "0.3.0"]

    [io.pedestal/pedestal.jetty "0.3.0"]
    ;; [io.pedestal/pedestal.tomcat "0.3.0"]

    [ch.qos.logback/logback-classic "1.1.2" :exclusions [org.slf4j/slf4j-api]]
    [org.slf4j/jul-to-slf4j "1.7.7"]
    [org.slf4j/jcl-over-slf4j "1.7.7"]
    [org.slf4j/log4j-over-slf4j "1.7.7"]

    [org.clojure/data.json "0.2.5"]
    [com.novemberain/welle "3.0.0"]
    [clj.qrgen "0.2.0"]
    [selmer "0.6.9"]
    [clj-time "0.8.0"]
    [hashobject/hashids "0.2.0"]]

  :min-lein-version "2.0.0"

  :resource-paths ["config", "resources"]

  :test2junit-output-dir "target/test2junit"

  :profiles {
    :dev {:aliases {"run-dev" ["trampoline" "run" "-m" "qr.server/run-dev"]}
          :dependencies [[io.pedestal/pedestal.service-tools "0.3.0"]]
          :plugins [[test2junit "1.1.3"]
                    [lein-bikeshed "0.1.7"]
                    [lein-vanity "0.2.0"]
                    [lein-cloverage "1.0.2"]]}
    :uberjar {:main qr.server
      :aot :all
      :dependencies [[io.pedestal/pedestal.jetty "0.3.0"]]}}

  :main ^{:skip-aot true} qr.server)
