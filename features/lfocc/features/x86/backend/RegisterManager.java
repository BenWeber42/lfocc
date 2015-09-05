package lfocc.features.x86.backend;

import java.util.ArrayList;
import java.util.List;

public class RegisterManager {
	
	private List<Register> free = new ArrayList<Register>();
	private List<Register> taken = new ArrayList<Register>();
	
	public RegisterManager() {
		free.add(Register.eax);
		free.add(Register.ebx);
		free.add(Register.ecx);
		free.add(Register.edx);
		free.add(Register.edi);
		free.add(Register.esi);
	}
	
	public boolean hasRegister() {
		return !free.isEmpty();
	}
	
	public Register acquire() {
		assert hasRegister();
		
		Register reg = free.get(free.size() - 1);
		acquire(reg);
		return reg;
	}
	
	public void acquire(Register reg) {
		assert free.contains(reg);
		assert !taken.contains(reg);

		free.remove(reg);
		taken.add(reg);
	}
	
	public void free(Register reg) {
		assert !free.contains(reg);
		assert taken.contains(reg);
		
		taken.remove(reg);
		free.add(reg);
	}
	
	public String push(Register reg) {
		free(reg);
		return "   pushl %" + reg + "\n";
	}
	
	public String pop(Register reg) {
		acquire(reg);
		return "   popl %" + reg + "\n";
	}
	
	public boolean isFree(Register reg) {
		return free.contains(reg);
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
