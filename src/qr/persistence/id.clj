(ns qr.persistence.id
  (:require [hashids.core :as hashid]
    [clj-time.local :as time]
    [clj-time.coerce :as time-coerce]))

(def ^:const SALT "e0a5c4a26051a37e0402b63a7df906a5")

(defn get-timestamp
  []
  (time-coerce/to-long (time/local-now)))

(defn generate-id
  [exists?]
  (loop []
    (let [time-id (hashid/encrypt (get-timestamp) SALT)]
      (if (not (exists? time-id))
        time-id
        (recur)))))