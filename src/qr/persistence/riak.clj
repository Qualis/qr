(ns qr.persistence.riak
  (:require [clojurewerkz.welle.core :as wc]
            [clojurewerkz.welle.buckets :as wb]
            [clojurewerkz.welle.kv :as kv]
            [qr.persistence.id :as id-generator]))

(defn get-value
  "get destination from record"
  [record]
  (get (get record :result) :value))

(defn create-record
  "create record"
  [url]
  (let [conn (wc/connect) bucket "resource" id (id-generator/generate-id)]
    (wb/update conn bucket)
    (kv/store conn bucket id {:destination url}
      {:content-type "application/clojure"})
      id))

(defn read-record
  "read record"
  [id]
  (let [conn (wc/connect) bucket "resource"]
    (wb/update conn bucket)
    (kv/fetch-one conn bucket id)))