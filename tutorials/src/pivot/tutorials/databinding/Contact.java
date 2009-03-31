/*
 * Copyright (c) 2009 VMware, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pivot.tutorials.databinding;

public class Contact {
	private String id;
	private String name;
	private Address address;
	private String phoneNumber;
	private String emailAddress;
	private IMAccount imAccount;

	public Contact() {
		this(null, null, new Address(), null, null, new IMAccount());
	}

	public Contact(String id, String name, Address address, String phoneNumber,
		String emailAddress, IMAccount imAccount) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
		this.imAccount = imAccount;
	}

	public String getID() {
		return id;
	}

	public String getId() {
		return getID();
	}

	public String getName() {
		return name;
	}

	public Address getAddress() {
		return address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public IMAccount getIMAccount() {
		return imAccount;
	}

	public IMAccount getImAccount() {
		return getIMAccount();
	}
}
