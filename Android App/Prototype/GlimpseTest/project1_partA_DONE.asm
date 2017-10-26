# Coleman Rogers - EEL 3801C Wednesday Lab
# PID: c3567049

.data
space: .asciiz "\n"
msg1: .asciiz "f_ten = "
msg2: .asciiz "f_two = "
msg3: .asciiz "g_ten = "
msg4: .asciiz "g_two = "

.text
# Grab 4 values and store them in $t0-t3
li $v0, 5
syscall

# A = t0, make a copy in s0
move $t0, $v0
add $s0, $t0, $zero
li $v0, 5
syscall

# B = t1, make a copy in s1
move $t1, $v0
add $s1, $t1, $zero
li $v0, 5
syscall

# C = t2, make a copy in s2
move $t2, $v0
add $s2, $t2, $zero
li $v0, 5
syscall

# D = t3, make a copy in s3
move $t3, $v0
add $s3, $t3, $zero

# $t9 being used as a dedicated counter for loops
li $t9, 1

# Handles squaring C = $t2
loop2:
addi $t9, $t9, 1
add $t2, $t2, $s2
blt $t9,$s2,loop2

# Reset counter to 1
li $t9, 1

# Handles cubing B = $t1, $s1 = input, $s4 = squared number
loop2_3:
addi $t9, $t9, 1
add $t1, $t1, $s1
blt $t9,$s1,loop2_3
move $s4, $t1

li $t9, 1

loop3:
addi $t9, $t9, 1
add $t1, $t1, $s4
blt $t9,$s1,loop3

li $t9, 1

# Handles raising A or $t4 to the 4th
loop4_2:
addi $t9, $t9, 1
add $t0, $t0, $s0
blt $t9,$s0,loop4_2
move $s5, $t0

li $t9, 1

loop4:
addi $t9, $t9, 1
add $t0, $t0, $s5
blt $t9,$s5,loop4

# Reset registers to 0 for re-use
li $s4, 0
li $t9, 0

# Multiply by 4 loop
timesFour:
addi $t9, $t9, 1
add $s4, $t1, $s4
blt $t9,4,timesFour

li $s5, 0
li $t9, 0

# Multiply by 3 loop
timesThree:
addi $t9, $t9, 1
add $s5, $t2, $s5
blt $t9,3,timesThree

li $s6, 0
li $t9, 1

# Multiply by 2 loop
timesTwo:
addi $t9, $t9, 1
add $s6, $t3, $s6
blt $t9,3,timesTwo

li $v0, 4
la $a0, msg1
syscall

# Perform order of operations on function
sub $t8, $t0, $s4
add $t8, $t8, $s5
sub $t8, $t8, $s6
move $a0, $t8

# Print result in decimal
li $v0, 1
syscall

li $v0, 4
la $a0, space
syscall

li $v0, 4
la $a0, msg2
syscall

# Print result in binary
li $v0, 35
syscall

li $t9, 0
# reset t1 before using
li $t1, 0

# get b^2 and store it in t1
loop2b:
addi $t9, $t9, 1
add $t1, $t1, $s1
blt $t9,$s1,loop2b

li $t9, 1

# get d^3 and store it in t3
loop3d:
addi $t9, $t9, 1
add $t3, $t3, $s3
blt $t9,$s3,loop3d
move $s4, $t3

li $t9, 1

loop3d2:
addi $t9, $t9, 1
add $t3, $t3, $s4
blt $t9,3,loop3d2

li $t9, 0

# AB^2 result stored in s7
timesA:
addi $t9, $t9, 1
add $s7, $t1, $s7
blt $t9,$s0,timesA

li $t9, 0
li $s6, 0

# C^2D^3 result stored in s6
timesC:
addi $t9, $t9, 1
add $s6, $t2, $s6
blt $t9,$t3,timesC

li $v0, 4
la $a0, space
syscall

li $v0, 4
la $a0, msg3
syscall

# Perform order of operations on function
add $t8, $s7, $s6
move $a0, $t8

# Print result in decimal
li $v0, 1
syscall

li $v0, 4
la $a0, space
syscall

li $v0, 4
la $a0, msg4
syscall

# Print result in binary
move $a0, $t8
li $v0, 35
syscall

# End
li $v0, 10
syscall