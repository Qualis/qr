(ns qr.persistence.id)

(defn generate-id
  "generate id for record"
  []
  (str (java.util.UUID/randomUUID)))
