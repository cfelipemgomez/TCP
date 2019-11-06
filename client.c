//#include <openssl/applink.c>
#include <openssl/bio.h>
#include <openssl/ssl.h>
#include <openssl/err.h>
//////////////////
#include <netdb.h> 
#include <stdio.h> 
#include <stdlib.h> 
#include <string.h> 
#include <sys/socket.h> 
#define MAX 300 
#define PORT 1099 
#define SA struct sockaddr 

void InitializeSSL()
{
    SSL_load_error_strings();
    SSL_library_init();
    OpenSSL_add_all_algorithms();
}

void DestroySSL()
{
    ERR_free_strings();
    EVP_cleanup();
}

void ShutdownSSL(SSL *cSSL)
{
    SSL_shutdown(cSSL);
    SSL_free(cSSL);
}

void func(int sockfd, SSL *cSSL) 
{ 
	char buff[MAX]; 
	int n; 
	for (;;) { 
		bzero(buff, sizeof(buff)); 
		printf("Enter data to send : \n\t"); 
		n = 0; 
		while ((buff[n++] = getchar()) != '\n'); 
		SSL_write(cSSL, buff, sizeof(buff)); 
		printf("Se hizo el envío");
		//write(sockfd, buff, sizeof(buff)); 

		bzero(buff, sizeof(buff)); 
		read(sockfd, buff, sizeof(buff)); 
		printf("Server response : %s", buff); 

		printf("\nClient Exit...\n"); 
		break;
		/*if ((strncmp(buff, "exit", 4)) == 0) { 
			printf("Client Exit...\n"); 
			break; 
		} */
	} 
} 

int main() 
{ 
//int sockfd, newsockfd;
SSL_CTX *sslctx;
SSL *cSSL;


	int sockfd, connfd; 
	struct sockaddr_in servaddr, cli; 

InitializeSSL();

	// socket create and varification 
	sockfd = socket(AF_INET, SOCK_STREAM, 0); 
	if (sockfd == -1) { 
		printf("socket creation failed...\n"); 
		exit(0); 
	} 
	else
		printf("Socket successfully created..\n"); 
	bzero(&servaddr, sizeof(servaddr)); 

	// assign IP, PORT 
	servaddr.sin_family = AF_INET; 
	servaddr.sin_addr.s_addr = inet_addr("127.0.0.1"); 
	servaddr.sin_port = htons(PORT); 

	// connect the client socket to server socket 
	if (connect(sockfd, (SA*)&servaddr, sizeof(servaddr)) != 0) { 
		printf("connection with the server failed...\n"); 
		exit(0); 
	} 
	else
		printf("connected to the server..\n"); 

sslctx = SSL_CTX_new( SSLv23_server_method());
SSL_CTX_set_options(sslctx, SSL_OP_SINGLE_DH_USE);
int use_cert = SSL_CTX_use_certificate_file(sslctx, "cert.pem" , SSL_FILETYPE_PEM);

int use_prv = SSL_CTX_use_PrivateKey_file(sslctx, "key.pem", SSL_FILETYPE_PEM);

cSSL = SSL_new(sslctx);
SSL_set_fd(cSSL, sockfd );
	// function for chat 
	func(sockfd, cSSL); 

	// close the socket 
	close(sockfd); 
} 

