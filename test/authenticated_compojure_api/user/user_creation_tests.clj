(ns authenticated-compojure-api.user.user-creation-tests
  (:require [clojure.test :refer :all]
            [authenticated-compojure-api.handler :refer :all]
            [authenticated-compojure-api.test-utils :refer [parse-body]]
            [authenticated-compojure-api.queries.query-defs :as query]
            [authenticated-compojure-api.test-utils :as helper]
            [ring.mock.request :as mock]
            [cheshire.core :as ch]
            [clj-time.core :as t]
            [clj-time.coerce :as c]))

(defn create-user [user-map]
  (app (-> (mock/request :post "/api/user" (ch/generate-string user-map))
           (mock/content-type "application/json"))))

(defn assert-no-dup [user-1 user-2 expected-error-message]
  (let [_        (create-user user-1)
        response (create-user user-2)
        body     (parse-body (:body response))]
    (is (= 409                    (:status response)))
    (is (= 1                      (count (query/all-registered-users query/db))))
    (is (= expected-error-message (:error body)))))

(defn setup-teardown [f]
  (try
    (query/insert-permission! query/db {:permission "basic"})
    (f)
    (finally (query/truncate-all-tables-in-database! query/db))))

(use-fixtures :once helper/create-tables)
(use-fixtures :each setup-teardown)

(deftest can-successfully-create-a-new-user-who-is-given-basic-permission-as-default
  (testing "Can successfully create a new user who is given basic permission as default"
    (is (= 0 (count (query/all-registered-users query/db))))
    (let [response            (create-user {:email "new@user.com" :username "NewUser" :password "pass"})
          body                (parse-body (:body response))
          new-registered-user (query/get-registered-user-details-by-username query/db {:username (:username body)})
          registered-at       (subs (str (c/to-long (:created_on new-registered-user))) 0 8)
          expected-time       (subs (str (c/to-long (t/now))) 0 8)]
      (is (= 201           (:status response)))
      (is (= 1             (count (query/all-registered-users query/db))))
      (is (= "NewUser"     (:username body)))
      (is (= "NewUser"     (str (:username new-registered-user))))
      (is (= expected-time registered-at) )
      (is (= "basic"       (:permissions new-registered-user))))))

(deftest can-not-create-a-user-if-username-already-exists-using-the-same-case
  (testing "Can not create a user if username already exists using the same case"
    (assert-no-dup {:email "Jrock@Taylor.com"   :username "Jarrod" :password "pass"}
                   {:email "Jam@Master.com"     :username "Jarrod" :password "pass"}
                   "Username already exists")))

(deftest can-not-create-a-user-if-username-already-exists-using-mixed-case
  (testing "Can not create a user if username already exists using mixed case"
    (assert-no-dup {:email "Jrock@Taylor.com"   :username "jarrod" :password "pass"}
                   {:email "Jam@Master.com"     :username "Jarrod" :password "pass"}
                   "Username already exists")))

(deftest can-not-create-a-user-if-email-already-exists-using-the-same-case
  (testing "Can not create a user if email already exists using the same case"
    (assert-no-dup {:email "jarrod@taylor.com" :username "Jarrod"   :password "the-first-pass"}
                   {:email "jarrod@taylor.com" :username "JarrodCT" :password "the-second-pass"}
                   "Email already exists")))

(deftest can-not-create-a-user-if-email-already-exists-using-mixed-case
  (testing "Can not create a user if email already exists using mixed case"
    (assert-no-dup {:email "wOnkY@email.com" :username "Jarrod" :password "Pass"}
                   {:email "WonKy@email.com" :username "Jrock"  :password "Pass"}
                   "Email already exists")))

(deftest can-not-create-a-user-if-username-and-email-already-exist-using-same-and-mixed-case
  (testing "Can not create a user if username and email already exist using same and mixed case"
    (assert-no-dup {:email "wOnkY@email.com" :username "jarrod" :password "pass"}
                   {:email "WonKy@email.com" :username "jarrod" :password "pass"}
                   "Username and Email already exist")))
