package lfocc.features.x86.backend;

import java.util.ArrayList;
import java.util.List;

public class RegisterManager {
	
	private List<Register> available = new ArrayList<Register>();
	private List<Register> taken = new ArrayList<Register>();
	private List<List<Register>> registerStack = new ArrayList<List<Register>>();
	
	public RegisterManager() {
		available.add(Register.eax);
		available.add(Register.ebx);
		available.add(Register.ecx);
		available.add(Register.edx);
		available.add(Register.edi);
		available.add(Register.esi);
	}
	
	public boolean hasRegister() {
		return !available.isEmpty();
	}
	
	public int freeRegisters() {
		return available.size();
	}
	
	public Register acquireRegister() {
		assert hasRegister();
		
		Register reg = available.remove(available.size() - 1);
		taken.add(reg);
		return reg;
	}
	
	public void releaseRegister(Register reg) {
		assert !available.contains(reg);
		assert taken.contains(reg);
		
		taken.remove(reg);
		available.add(reg);
	}
	
	/**
	 * Generate x86 assembly until num registers are free
	 * (see {@link #popRegisters()}
	 * 
	 * Warning: {@link #popRegisters()} and @{link {@link #pushRegisters(int)}
	 * have to be called in a stack fashion.
	 */
	public String pushRegisters(int num) {
		List<Register> regs = new ArrayList<Register>();
		String src = "";
		
		while (freeRegisters() < num) {
			Register reg = getAcquiredRegister();
			regs.add(reg);
			releaseRegister(reg);
			src += "   pushl %" + reg + "\n";
		}
		
		registerStack.add(regs);
		return src;
	}
	
	/**
	 * Generates x86 assembly that will pop the previously saved registers
	 * (see {@link #pushRegisters(int)})
	 * 
	 * Warning: {@link #popRegisters()} and @{link {@link #pushRegisters(int)}
	 * have to be called in a stack fashion.
	 */
	public String popRegisters() {
		assert registerStack.size() >= 1;
		
		List<Register> regs = registerStack.remove(registerStack.size() - 1);
		String src = "";
		
		for (Register reg: regs)
			src = "   popl %" + reg + "\n" + src;
		
		return src;
	}
	
	private Register getAcquiredRegister() {
		return taken.get(taken.size() - 1);
	}
	
	public enum Register {
		eax("eax"),
		ebx("ebx"),
		ecx("ecx"),
		edx("edx"),
		esi("esi"),
		edi("edi");
		
		private final String name;
		
		private Register(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
