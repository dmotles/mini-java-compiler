package mips.allocator;

public enum Register {
    zero(0, true),
    at(1, true),
    v0(2,true),
    v1(3,true),
    a0(4,true),
    a1(5,true),
    a2(6,true),
    a3(7,true),
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
    ra(31, true),
    MEM_STACK_FRAME(99,true),
    MEM_CLASS(101,true),
    MEM_SPILL(99,true);



    public int num;
    public boolean reserved;
    public ColorAllocation allocation;

    Register( int num ) {
        this.num = num;
        this.reserved = false;
    }

    Register( int num , boolean reserved ) {
        this.num = num;
        this.reserved = reserved;
    }

}
