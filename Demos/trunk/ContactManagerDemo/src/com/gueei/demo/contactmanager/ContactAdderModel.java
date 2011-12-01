package com.gueei.demo.contactmanager;

import gueei.binding.Command;
import gueei.binding.Observable;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.StringObservable;
import gueei.binding.validation.ModelValidator;
import gueei.binding.validation.ValidationResult;
import gueei.binding.validation.validators.RegexMatch;
import gueei.binding.validation.validators.Required;
import android.app.Activity;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

public class ContactAdderModel{
	private Activity mContext;
	
	@Required
	public StringObservable Name = new StringObservable();
	
	@RegexMatch(Pattern = "(^\\d{6,10}$)", ErrorMessage="Phone Number must be 5-10 digits")
	public StringObservable Phone = new StringObservable();
	
	@RegexMatch
		(Pattern="^(([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5}){1,25})+([;.](([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5}){1,25})+)*$",
		 ErrorMessage="Invalid Email")
	public StringObservable Email = new StringObservable();
	
	public ArrayListObservable<PhoneLabelPair> PhoneTypeList = new ArrayListObservable<PhoneLabelPair>(PhoneLabelPair.class); 
	public ArrayListObservable<EmailLabelPair> EmailTypeList = new ArrayListObservable<EmailLabelPair>(EmailLabelPair.class);
	public Observable<PhoneLabelPair> SelectedPhoneType = new Observable<PhoneLabelPair>(PhoneLabelPair.class);
	public Observable<EmailLabelPair> SelectedEmailType = new Observable<EmailLabelPair>(EmailLabelPair.class);

	public Command SaveContact = new Command(){
		public void Invoke(View view, Object... args) {
			ValidationResult result = ModelValidator.ValidateModel(ContactAdderModel.this);
			String message;
			if (result.isValid()){
				message = "Here save is not going to perform, what you have entered: \n" +
						"Name: " + Name.get() + "\n" +
						"Phone: " + Phone.get() + "(" +  SelectedPhoneType.get().Label.get() + ")\n" +
						"Email: " + Email.get() + "(" +  SelectedEmailType.get().Label.get() + ")\n";
			}
			else{
				message = "Validation Error: \n";
				for(String error : result.getValidationErrors()){
					message += error + "\n";
				}
			}
			(Toast.makeText(mContext, message, Toast.LENGTH_LONG)).show();
		}
	};
	
	public ContactAdderModel(Activity context){
		mContext = context;
		PhoneLabelPair[] PhoneTypes = new PhoneLabelPair[]{
				new PhoneLabelPair(ContactsContract.CommonDataKinds.Phone.TYPE_HOME),
				new PhoneLabelPair(ContactsContract.CommonDataKinds.Phone.TYPE_WORK),
				new PhoneLabelPair(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE),
				new PhoneLabelPair(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER)
		};
		EmailLabelPair[] EmailTypes = new EmailLabelPair[]{
				new EmailLabelPair(ContactsContract.CommonDataKinds.Email.TYPE_HOME),
				new EmailLabelPair(ContactsContract.CommonDataKinds.Email.TYPE_WORK),
				new EmailLabelPair(ContactsContract.CommonDataKinds.Email.TYPE_MOBILE),
				new EmailLabelPair(ContactsContract.CommonDataKinds.Email.TYPE_OTHER)
		};
		PhoneTypeList.setArray(PhoneTypes);
		EmailTypeList.setArray(EmailTypes);
	}
		
	private class EmailLabelPair{
		public StringObservable Label = new StringObservable();
		public EmailLabelPair(int kind){
			Label.set(ContactsContract.CommonDataKinds.Email
				.getTypeLabel(mContext.getResources(), kind, 
						mContext.getString(R.string.undefinedTypeLabel).toString()).toString());
		}
	}
	
	private class PhoneLabelPair{
		public StringObservable Label = new StringObservable();
		public PhoneLabelPair(int kind){
			Label.set(ContactsContract.CommonDataKinds.Phone
				.getTypeLabel(mContext.getResources(), kind, 
						mContext.getString(R.string.undefinedTypeLabel).toString()).toString());
		}
	}
}
