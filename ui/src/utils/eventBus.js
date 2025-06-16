class EventBus {
    constructor() {
      this.events = {};
    }
  
    on(eventName, fn) {
      if (!this.events[eventName]) {
        this.events[eventName] = [];
      }
      this.events[eventName].push(fn);
    }
  
    emit(eventName, data) {
      if (this.events[eventName]) {
        this.events[eventName].forEach(function(fn) {
          fn(data);
        });
      }
    }
  
    off(eventName, fn) {
      if (this.events[eventName]) {
        if (!fn) {
          delete this.events[eventName];
        } else {
          this.events[eventName] = this.events[eventName].filter(f => f !== fn);
        }
      }
    }
  }
  
  const eventBus = new EventBus();
  export default eventBus;