int main(int arg) {
    int r = randomInt();
    print r;
    if (r < 0) {
        print 0 - r;
    } else {
        print r;
    }
    return r;
}

