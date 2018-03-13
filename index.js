import { NativeModules, Platform } from 'react-native';

const RNSumUpWrapper = NativeModules.RNSumUp;

const RNSumUp = {
  apiKey: '',

  setup(key) {
    this.apiKey = key;
    if (Platform.OS === 'ios') {
      RNSumUpWrapper.setup(key);
    }
  },

  authenticate() {
    return (Platform.OS === 'ios') ? RNSumUpWrapper.authenticate() : RNSumUpWrapper.authenticate(this.apiKey);
  },

  authenticateWithToken(token) {
    return (Platform.OS === 'ios') ? RNSumUpWrapper.authenticate(token) : RNSumUpWrapper.authenticate(this.apiKey, token);
  },

  logout() {
    this.isLoggedIn = false;
    return RNSumUpWrapper.logout();
  },

  prepareForCheckout() {
    return RNSumUpWrapper.prepareForCheckout();
  },

  checkout(request) {
    return (Platform.OS === 'ios') ? RNSumUpWrapper.checkout(request) : RNSumUpWrapper.checkout(this.apiKey, request.totalAmount, request.name);
  },

  preferences() {
    return RNSumUpWrapper.preferences();
  },

  isLoggedIn() {
    return RNSumUpWrapper.isLoggedIn();
  }
};

module.exports = RNSumUp;
