import { NativeModules, Platform } from 'react-native';

const RNSumUpWrapper = NativeModules.RNSumUp;

const RNSumUp = {
  apiKey: '',
  isLoggedIn: false,

  setup(key) {
    this.apiKey = key;
    if (Platform.OS === 'ios') {
      RNSumUpWrapper.setup(key);
    }
  },

  authenticate() {
    return (Platform.OS === 'ios') ? RNSumUpWrapper.authenticate() : RNSumUpWrapper.authenticate(this.apiKey);
  },

  logout() {
    return RNSumUpWrapper.logout();
  },

  prepareForCheckout() {
    return RNSumUpWrapper.prepareForCheckout();
  },

  checkout(request) {
    return (Platform.OS === 'ios') ? RNSumUpWrapper.checkout(request) : RNSumUpWrapper.checkout(this.apiKey, request.totalAmount, request.name);
  },

  isLoggedIn() {
    return (Platform.OS === 'ios') ? RNSumUpWrapper.isLoggedIn() : this.isLoggedIn();
  }
};

module.exports = RNSumUp;
