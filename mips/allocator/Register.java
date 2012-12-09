package mips.allocator;

public enum Register {
    zero(0, true),
    at(1, true),
    v0(2),
    v1(3),
    a0(4),
    a1(5),
    a2(6),
    a3(7),
    t0(8),
    t1(9),
    t2(10),
    t3(11),
    t4(12),
    t5(13),
    t6(14),
    t7(15),
    s0(16),
    s1(17),
    s2(18),
    s3(19),
    s4(20),
    s5(21),
    s6(22),
    s7(23),
    t8(24),
    t9(25),
    k0(26, true),
    k1(27, true),
    gp(28, true),
    sp(29, true),
    fp(30, true),
    ra(31, true);




    public int num;
    public boolean reserved;

    Register( int num ) {
        this.num = num;
        this.reserved = false;
    }

    Register( int num , boolean reserved ) {
        this.num = num;
        this.reserved = reserved;
    }
}
