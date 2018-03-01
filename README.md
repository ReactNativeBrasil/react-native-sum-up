
# react-native-sum-up

## Getting started

`$ npm install react-native-sum-up --save`

### Mostly automatic installation

`$ react-native link react-native-sum-up`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-sum-up` and add `RNSumUp.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNSumUp.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNSumUpPackage;` to the imports at the top of the file
  - Add `new RNSumUpPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-sum-up'
  	project(':react-native-sum-up').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-sum-up/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-sum-up')
  	```
## Usage
```javascript
import RNSumUp from 'react-native-sum-up';

// TODO: What to do with the module?
RNSumUp;
```
  