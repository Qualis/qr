(ns qr.io.image
  (:require [clj.qrgen :as qr]))

(defn qr-for
  [url]
  (qr/as-bytes (qr/from url)))