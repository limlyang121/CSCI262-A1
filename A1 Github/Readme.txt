Tested on Ubuntu - wsl2
javac version
11.0.16

To Compile
javac Rainbow.java

To Execute
java Rainbow Password.txt

Both
javac Rainbow.java && java Rainbow Password.txt


How Reduction work:
To get Reduction for each Password:
1. Hash the password with MD5
2. Convert the Hash value to Decimal with BigInteger (hash ,16) <- in my case using java.
Then we mod it with txt totalLine
3. Add 1 to the decimal <- Formula

==========
4. Let explain using 10th. Since it reduction is 15, we will Go to the password number (15 -1) <- Because arrayList start at 0
5. Grab the password and hash it. Get it reduction size using step number 2 and 3
6. Once got the reduction size, repeat step 4 and go to password number (the reduction size)
7. Repeat the process for 4 times (<- In this assignment use 4 times, but can change)

 
