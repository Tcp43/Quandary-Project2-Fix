int three() { return 3; }
int two()   { return 2; }

int main(int arg) {
    print -1 * (two() + three()); 
    print (1 + two()) * three();  
    print 1 + two() * three();    
    return 0;
}