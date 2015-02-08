(ns authenticated-compojure-api.route-functions.user
  (:require [authenticated-compojure-api.auth-resources.auth-key :refer [auth-key]]
            [authenticated-compojure-api.queries.query-defs :as query]
            [buddy.sign.generic :as bs]
            [buddy.hashers.bcrypt :as hasher]
            [ring.util.http-response :refer [bad-request ok created conflict]]))

(defn auth-credentials-response [request]
  (ok (let [user    (:identity request)
            token   (bs/dumps user auth-key)
            refresh (:refresh_token user)]
        {:username (:username user) :token token :refresh_token refresh})))

(defn gen-new-token-response [refresh_token]
  (let [user (query/get-user-by-reset-token {:refresh_token refresh_token})]
    (if (empty? user)
      (bad-request {:error "Bad Request"})
      (ok {:token (bs/dumps user auth-key)}))))

(defn create-new-user [email username password]
  (let [refresh-token   (str (java.util.UUID/randomUUID))
        hashed-password (hasher/make-password password)
        new-user        (query/insert-user<! {:email         email
                                              :username      username
                                              :password      hashed-password
                                              :refresh_token refresh-token})
        permission      (query/insert-permission-for-user<! {:userid     (:id new-user)
                                                             :permission "basic"})]
    (created {:username (str (:username new-user))})))

(defn create-user-response [email username password]
  (let [username-query (query/get-registered-user-by-username {:username username})
        email-query    (query/get-registered-user-by-email {:email email})]
    (cond
      (and (not-empty username-query) (not-empty email-query)) (conflict {:error "Username and Email already exist"})
      (not-empty username-query) (conflict {:error "Username already exists"})
      (not-empty email-query) (conflict {:error "Email already exists"})
      :else (create-new-user email username password))))
