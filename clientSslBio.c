 #include <openssl/bio.h>

 int main(int count, char *strings[])
{
 BIO *cbio, *out;
 BIO_ADDR *addr;
 int len;
 char tmpbuf[1024];

 cbio = BIO_new_connect("localhost:tcp");
 BIO_set_conn_port(cbio, htons(1099));
 
 BIO_set_conn_address(cbio, BIO_ADDR *addr);
 BIO_set_conn_ip_family(cbio, AF_INET);
 out = BIO_new_fp(stdout, BIO_NOCLOSE);
 if (BIO_do_connect(cbio) <= 0) {
     fprintf(stderr, "Error connecting to server\n");
     ERR_print_errors_fp(stderr);
     exit(1);
 }
 BIO_puts(cbio, "GET / HTTP/1.0\n\n");
 for (;;) {
     len = BIO_read(cbio, tmpbuf, 1024);
     if (len <= 0)
         break;
     BIO_write(out, tmpbuf, len);
 }
 BIO_free(cbio);
 BIO_free(out);
}