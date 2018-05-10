### Environments:

* Framework: Springboot, Hibernate
* JAX-RS implementation: Jersey
* Specification: Swagger Editor, Swagger CodeGen, OpenApi 3.0
* Database: PostgresSQL
* Local: Mac, MiniKube, Docker, ZuluJDK
* Google Cloud platform: Kubernetes engine, Cloud SQL, Load Balancing, Container Registry, Cloud Source Repositories, Logging 

##### Ecosystem of Development on this project

Loop:

1. Draft specification using OpenApi 3.0.  
   artifact: **_doc/openapi.yaml_**

2. Generate server stub using Swagger CodeGen CLI.  
   artifacts: **_src/main/java/io.swagger.api.gen.api/_**

3. Complete the implementation of the JAX-RS API.   
   artifacts: **_src/main/java/io.swagger.api.impl/_**

4. Git Push.

5. Continuous deployment to Kubernetes Engine using Jenkins.    
   artifacts: **_k8s/_**, **_Dockerfile_**, **_Jenkinsfile_**

6. Generate documentation and client library using Swagger CodeGen CLI.    
   artifacts: **_doc/html2/index.html_**

---

### Phase 1: Sample Jersey RESTfull API application lunched up locally

##### Connecting to Cloud SQL: 
   
1. Stage: [Connecting from Kubernetes Engine](https://cloud.google.com/sql/docs/postgres/connect-kubernetes-engine)

2. Local: [Connecting psql Client Using the Cloud SQL Proxy](https://cloud.google.com/sql/docs/postgres/connect-admin-proxy) or Connecting from Kubernetes Engine
    
    Restart the proxy if disconnected. 
    1. Find the port used by poxy and kill the process
        ```sh
        $ [sudo] lsof -i :<PORT>
        $ kill -9 <PID>
        ```
    2. Restart the proxy 
        ```sh
        $ ./cloud_sql_proxy -instances=<INSTANCE_CONNECTION_NAME>=tcp:5432 \
                          -credential_file=<PATH_TO_KEY_FILE> &
         ```
    
    In my case:
    ```sh
    $ ./cloud_sql_proxy -instances=my-tw-zone-project:asia-east1:postgres-dev=tcp:5432 -credential_file=/Users/jerrylin/Desktop/Projects/credentials/credentials.json &
    ```
##### Package the Java application as a Docker image
```sh
$ ./mvnw -DskipTests package
$ docker build -t hello-java:v1 .
```

##### Deploy your application to Kubernetes

For single app container in a Pod:
```sh
$ kubectl run hello-java \
    --image=hello-java:v1 \
    --port=8080
```

For Kubernetes Deployment manifest file using Cloud SQL proxy
```sh
$ kubectl apply -f deployment.yaml
```

##### Allow external traffic

For single app container in a Pod:
```sh
$ kubectl expose deployment hello-java --type=LoadBalancer
```

For Kubernetes Deployment manifest file using Cloud SQL proxy
```sh
$ kubectl expose deployment myapp --type=LoadBalancer
```

Automatically opens up a browser window
```sh
$ minikube service myapp
```

##### Problem and possible solution set of Phase 1:

1. The application.properties file will be packaged. If the database configuration want to be set dynamically, redeployment will be needed. 
   
   Apply Kubernetes ConfigMaps or Secrets. 
      
    In application.properties file:
    
    ```sh 
    spring.datasource.url=jdbc:postgresql://${POSTGRES_DB_HOST}/postgres
    spring.datasource.username=${POSTGRES_DB_USER}
    spring.datasource.password=${POSTGRES_DB_PASSWORD}
    ```
    
    In the deployment yaml file: set up ENV for the java application
    
    ```sh 
    env:
        - name: POSTGRES_DB_HOST
          value: 127.0.0.1:5432
        # [START cloudsql_secrets]
        - name: POSTGRES_DB_USER
          valueFrom:
            secretKeyRef:
              name: cloudsql-db-credentials
              key: username
        - name: POSTGRES_DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: cloudsql-db-credentials
              key: password
    ```
    
    And, apply the secrets:
    ```sh 
    $ kubectl create secret generic cloudsql-db-credentials \
        --from-literal=username=[USERNAME] --from-literal=password=[PASSWORD]
    ```
2. What if the approval of schema creation from DBA needed? 
    
    Tools of version control database:
    * [liquibase](http://www.liquibase.org/) : Automatically generate SQL scripts for DBA code review
    * [flywaydb](https://flywaydb.org/) : check the link of feature comparison between Liquibase, MyBatis and Flywaydb
    
    [How to generate a ddl creation script with a modern Spring Boot + Data JPA and Hibernate setup?](https://stackoverflow.com/questions/36966337/how-to-generate-a-ddl-creation-script-with-a-modern-spring-boot-data-jpa-and-h)
    
---

### Phase 2: CI/CD Multibranch Pipeline

##### Jenkins on GCP setting up

* [Continuous Deployment to Kubernetes Engine using Jenkins](https://cloud.google.com/solutions/continuous-delivery-jenkins-kubernetes-engine)
* Modify **_JenkinsFile_** from the example provided.
    1. Testing
    2. Packaging
    3. Build image
    4. Push image to registry
    5. Deploy Application for corresponding branch

##### Configuration Depending on the Environment

* Set the Active Spring Profiles
  
  Create ConfigMaps from literal values
  ```sh
  $ kubectl create configmap application-properties-platforms \
    --from-literal=canary=canary \
    --from-literal=production=prod \
    --from-literal=dev=dev
  ```
  
  Apply the env **_PLATFORM_** from ConfigMaps in k8s deployment yaml file:
  ```sh
  env:
    - name: PLATFORM
      valueFrom:
        configMapKeyRef:
          # The ConfigMap containing the value you want to assign to PLATFORM
          name: application-properties-platforms
          # Specify the key associated with the value
          key: canary
  ```
  
  In application.properties, apply the env to let Springboot to get the corresponding properties file :
    ```sh
    spring.profiles.active=${PLATFORM}
    ```
  
  If the PLATFORM = canary, the **_application-canary.properties_** will be applied.

---

### Phase 3: Serving multiple applications on the same IP

Want to use the same IP/Domain for different service (deployment).

EX: {domain}/prod/* , {domain}/canary/* 

[Setting up HTTP Load Balancing with Ingress](https://cloud.google.com/kubernetes-engine/docs/tutorials/http-balancer)

[Name based virtual hosting Ingress](https://kubernetes.io/docs/concepts/services-networking/ingress/#name-based-virtual-hosting)
    
```sh
$ kubectl expose deployment booksearcher-prod --target-port=8080 --type=NodePort
$ kubectl expose deployment booksearcher-canary --target-port=8080 --type=NodePort
---
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: fanout-ingress
spec:
  rules:
  - host: booksearcher.codemonkey.zone
    http:
      paths:
      - backend:
          serviceName: booksearcher-prod
          servicePort: 8080
  - host: booksearchercanary.codemonkey.zone
    http:
      paths:
      - backend:
          serviceName: booksearcher-canary
          servicePort: 8080
```
Save it to a **_fanout-ingress.yaml_**, and run:

```sh
$ kubectl create -f fanout-ingress.yaml
```

---

### Phase 4: HTTPS

1. Register a domain name.

2. [Linking my domain to a google cloud project](https://tw.godaddy.com/community/Managing-Domains/linking-my-domain-to-a-google-cloud-project/td-p/13086)

3. Getting TLS/SSL certificates from Let’s Encrypt and refreshing them automatically

    [Tutorial for installing cert-manager to get HTTPS certificates from Let’s Encrypt by ahmetb](https://github.com/ahmetb/gke-letsencrypt)

4. Apply tls in k8s/fanout-ingress.yaml

---

### TODO LIST

1. Logging with Google Pub/Sub, [ELK stack](https://www.elastic.co/elk-stack), [Dropwizard Metrics](https://github.com/dropwizard/metrics).

2. [K8S cronjob](https://kubernetes.io/docs/tasks/job/automated-tasks-with-cron-jobs/) practice.

### Cheat Sheet
Open the Kubernetes dashboard in a browser:
```sh
$ minikube dashboard
```

View the Deployment:
```sh
$ kubectl get deployments
```

View the Service you just created:
```sh
kubectl get services
```

View the Pod:
```sh
$ kubectl get pods
```

To see some logs:
```sh
$ kubectl logs <POD-NAME>
```

Swagger Codegen for server stub/client library cmd
```sh
$ java -jar {swagger-codegen.jar path} generate \ 
-i {yaml path} \  #openapi.yaml
-l {stub type} \  #jaxrs-jersey
-o {output path} \ 
```

 
### References
* [Deploy a Java application to Kubernetes on Google Kubernetes Engine](https://codelabs.developers.google.com/codelabs/cloud-springboot-kubernetes/index.html#0)
* https://github.com/spring-guides/gs-spring-boot-docker
* [Hello Minikube](https://kubernetes.io/docs/tutorials/stateless-application/hello-minikube/)
* [Using minikube as a “docker-machine” to avoid sharing a local-registry](https://blog.hasura.io/using-minikube-as-a-docker-machine-to-avoid-sharing-a-local-registry-bf5020b8197)

##### Java
* [Transform object into another type with Java 8](https://www.leveluplunch.com/java/tutorials/016-transform-object-class-into-another-type-java8/)
* [Spring Boot and Jersey (JAX-RS) static files support](https://www.geekmj.org/jersey/spring-boot-jersey-static-web-files-support-403/)
* [Maven Surefire Plugin / Using TestNG](http://maven.apache.org/surefire/maven-surefire-plugin/examples/testng.html)

##### Swagger
* [Swagger Codegen server stub](https://github.com/swagger-api/swagger-codegen/wiki/Server-stub-generator-HOWTO)

##### K8S
* [kubectl CheatSheet](https://kubernetes.io/docs/reference/kubectl/cheatsheet/)
* [Continuous Deployment to Kubernetes Engine using Jenkins](https://cloud.google.com/solutions/continuous-delivery-jenkins-kubernetes-engine)
* https://github.com/GoogleCloudPlatform/continuous-deployment-on-kubernetes
* [Setting up Jenkins on Kubernetes Engine](https://cloud.google.com/solutions/jenkins-on-kubernetes-engine-tutorial)
* [Setting up HTTP Load Balancing with Ingress ](https://cloud.google.com/kubernetes-engine/docs/tutorials/http-balancer)
* [Tutorial for installing cert-manager to get HTTPS certificates from Let’s Encrypt by ahmetb](https://github.com/ahmetb/gke-letsencrypt)

##### Recommended Books
* [High-Performance Java Persistence](https://github.com/vladmihalcea/high-performance-java-persistence)