FROM qualis/qr:base

ADD qr.conf /etc/supervisor/conf.d/
ADD . /var/lib/qr

RUN cd /var/lib/qr && \
    lein deps

EXPOSE 8080
