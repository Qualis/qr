(ns qr.persistence.riak
  (:require [clojurewerkz.welle.core :as wc]
            [clojurewerkz.welle.buckets :as wb]
            [clojurewerkz.welle.kv :as kv]
            [qr.persistence.id :as id-generator]))

(defn save
  "save to store"
  [url]
  (let [conn (wc/connect) bucket "resource" id (id-generator/generate-id)]
    (wb/create conn bucket)
    (kv/store  conn bucket id {:destination url}
      {:content-type "application/clojure"})
      id))