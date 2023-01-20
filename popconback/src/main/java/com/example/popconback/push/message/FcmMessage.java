package com.example.popconback.push.message;

public class FcmMessage {
    private boolean validate_only;
    private Message message;
    
    public FcmMessage() {}
    public FcmMessage(boolean validate_only, Message message) {
		super();
		this.validate_only = validate_only;
		this.message = message;
	}


	public boolean isValidate_only() {
		return validate_only;
	}


	public void setValidate_only(boolean validate_only) {
		this.validate_only = validate_only;
	}


	public Message getMessage() {
		return message;
	}


	public void setMessage(Message message) {
		this.message = message;
	}


	/** Message
	 * 
	 * @author USER1
	 *
	 */
	public static class Message {
        private Notification notification;
        private String token;
        
        public Message() {}
        
		public Message(Notification notification, String token) {
			super();
			this.notification = notification;
			this.token = token;
		}
		public Notification getNotification() {
			return notification;
		}
		public void setNotification(Notification notification) {
			this.notification = notification;
		}
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}

    }

	/** Notification
	 * 
	 * @author USER1
	 *
	 */
    public static class Notification {
        private String title;
        private String body;
        private String image;
        
        public Notification() {}
        
		public Notification(String title, String body, String image) {
			super();
			this.title = title;
			this.body = body;
			this.image = image;
		}
		
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getBody() {
			return body;
		}
		public void setBody(String body) {
			this.body = body;
		}
		public String getImage() {
			return image;
		}
		public void setImage(String image) {
			this.image = image;
		}
        
        
        
        
    }
}
