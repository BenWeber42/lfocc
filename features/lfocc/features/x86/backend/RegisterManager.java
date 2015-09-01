package lfocc.features.x86.backend;

import java.util.ArrayList;
import java.util.List;

public class RegisterManager {
	
	private List<Register> available = new ArrayList<Register>();
	
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
	
	public Register getRegister() {
		assert hasRegister();
		
		return available.remove(available.size() - 1);
	}
	
	public void freeRegister(Register reg) {
		assert !available.contains(reg);
		
		available.add(reg);
	}

	public enum Register {
		eax("eax"),
		ebx("ebx"),
		ecx("ecx"),
		edx("edx"),
		esi("esi"),
		edi("edi");
		
		private String name;
		
		private Register(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
