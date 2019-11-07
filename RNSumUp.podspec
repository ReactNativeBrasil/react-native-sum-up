require "json"

Pod::Spec.new do |s|
  # NPM package specification
  package = JSON.parse(File.read(File.join(File.dirname(__FILE__), "package.json")))

  s.name         = "RNSumUp"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = "https://github.com/ReactNativeBrasil/react-native-sum-up"
  s.license      = "MIT"
  s.author       = { package["author"]["name"] => package["author"]["email"] }
  s.platforms    = { :ios => "9.0" }
  s.source       = { :git => "https://github.com/ReactNativeBrasil/react-native-sum-up.git", :tag => "#{s.version}" }
  s.source_files = "ios/**/*.{h,m}"

  s.pod_target_xcconfig    = {
    'HEADER_SEARCH_PATHS' => [
      '"$(SRCROOT)/../**"'
    ]
  }

  s.dependency "React"
  s.dependency "SumUpSDK"

end
