(ns pandeiro.boot-http.util)

(defn resolve-and-invoke [sym]
  (require (symbol (namespace sym)) :reload)
  (let [sym-fn (resolve sym)]
    (sym-fn)))
