package org.unibl.etf.mdp.chat.server;

public class Pair<L,R> {
    private L l;
    private R r;
    public Pair(L l, R r){
        this.l = l;
        this.r = r;
    }
    public L getL(){ return l; }
    public R getR(){ return r; }
    public void setL(L l){ this.l = l; }
    public void setR(R r){ this.r = r; }
    
    @Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (l == null)
			if(r != other.r)
				return false;
		else if(r == null)
			if(l != other.l)
				return false;
		if(l == null && r == null)
			return false;
		return true;
	}
}
