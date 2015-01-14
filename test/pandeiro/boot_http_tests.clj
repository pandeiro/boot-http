(ns pandeiro.boot-http-tests
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [pandeiro.boot-http.impl :refer :all]))

(deftest detect-index-files
  (let [index-html  (io/file "index.html")
        index-htm   (io/file "index.htm")
        INDEX-HTML  (io/file "INDEX.HTML")
        index-shtml (io/file "index.shtml")]
    (testing "recognize index files"
      (is (= index-html (index-file-exists? [index-html])))
      (is (= index-htm  (index-file-exists? [index-htm])))
      (is (= INDEX-HTML (index-file-exists? [INDEX-HTML]))))
    (testing "ignore non-index files"
      (is (nil? (index-file-exists? [index-shtml]))))))

