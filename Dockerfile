FROM redis:2.8.19

MAINTAINER Matt Cross <blacklightgfx@gmail.com>

EXPOSE 1882

ADD . /data

RUN sudo apt-get install openjdk-7-jre -y

CMD java -jar /data/target/scala-2.11/taskr.jar localhost 1882
