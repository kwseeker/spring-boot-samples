#include <stdio.h>
#include <unistd.h>
#include <signal.h>
#include <stdlib.h>

static void sig_term_handle(int sig) {
    printf("received signal %d\n", sig);
    if (sig == SIGTERM) {
        printf("process been terminated %d\n", sig);
        exit(1);
    }
}

static void sig_int_handle(int sig) {
    printf("received signal %d\n", sig);
    if (sig == SIGTERM) {
        printf("process been interrupted %d\n", sig);
        exit(2);
    }
}

/**
 * 先查当前进程的pid, 然后 kill <pid>
 */
int main() {
    if (signal(SIGTERM, sig_term_handle) == SIG_ERR ||
        signal(SIGINT, sig_int_handle) == SIG_ERR) {
        printf("signal error\n");
        exit(-1);
    }
    printf("register signal handler done!\n");

    for (int i = 1; i <= 1000; ++i) {
        sleep(1);
        printf("%d\n", i);
    }

    return 0;
}
