FROM redis:2.8.19

MAINTAINER Matt Cross <blacklightgfx@gmail.com>

EXPOSE 1882

RUN sudo yum install -y wget curl

RUN wget http://www.scala-lang.org/files/archive/scala-2.11.4.deb
RUN wget http://dl.bintray.com/sbt/debian/sbt-0.13.6.deb

RUN sudo dpkg -i scala-2.11.4.deb
RUN sudo dpkg -i sbt-0.13.6.deb

RUN sudo apt-get update
RUN sudo apt-get install -y git scala sbt openjdk-7-jre

RUN git clone https://github.com/mcross1882/taskr.git

CMD redis-server
    && cd /taskr && sbt clean compile test assembly
    && java -jar /taskr/target/scala-2.11/taskr.jar localhost 1882
