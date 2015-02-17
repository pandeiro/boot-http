(ns pandeiro.boot-http.util)

(defn resolve-sym [sym]
  (require (symbol (namespace sym)) :reload)
  (resolve sym))

(defn resolve-and-invoke [sym]
  (let [sym-fn (resolve-sym sym)]
    (sym-fn)))
