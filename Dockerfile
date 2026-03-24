FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

RUN apt update
RUN apt install -y openjdk-21-jdk
RUN apt install -y vlc
RUN apt install -y vlc-plugin-base
RUN apt install -y libvlc-dev
RUN apt install -y libvlccore-dev
RUN apt install -y pulseaudio-utils
RUN apt install -y alsa-utils
RUN apt install -y net-tools
RUN apt clean

WORKDIR /CSE471TermProject

COPY src src
COPY lib lib
COPY entrypoint.sh entrypoint.sh
RUN chmod +x entrypoint.sh
RUN sed -i 's/\r$//' entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]
