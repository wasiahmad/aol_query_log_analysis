# AOL Query Log Analysis
This project aims to analyze different aspects of the **AOL query log** and extract insightful information that may help to approach other research problems in **information retrieval**.

### Crawling all web Urls from AOL dataset
  * There are **1,632,797** URLs in AOL dataset and we have crawled **1,051,483** wchich is **64.4%** of the total number of URLs. Remaining URLs are either dead, broken or moved permanently to another URL. List of all the URLs can be found [here](https://drive.google.com/a/virginia.edu/file/d/0B8ZGlkqDw7hFNkc0c0p1OVF2YTA/view).

  We have stored the crawled data in one xml file and the format of the file looks like below.

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <crawledData>
     <page id="45" url="http://www.google.com/">
  		 <anchor>Null</anchor>
  		 <content>Search Images Maps Play YouTube News Gmail Drive More »Web History | Settings | Sign in × Try a fast, secure browser with updates built in. Yes, get Chrome now  Advanced searchLanguage toolsAdvertising ProgramsBusiness Solutions+GoogleAbout Google© 2016 - Privacy - Terms</content>
     </page>
     <page id="455" url="http://www.apple.com/">
  		 <anchor>Null</anchor>
  		 <content>Open Menu Close Menu Apple Shopping Bag Apple Mac iPad iPhone Watch TV Music Support Search apple.com Shopping Bag iPad Pro Super. Computer. NowÂ inÂ twoÂ sizes. Learn more Watch the film Watch the keynote iPhone SE A big step for small. Learn more Watch the keynote Apple Watch You. At a glance. Learn more Watch the keynote March Event 2016 Watch the keynote Apple and Education. Create more a-ha moments. iPhone 6s. 3D Touch. 12MP photos. 4K video. One powerful phone. Apple tv. The future of television is here. Macbook. Light. Years ahead. Better together. Shop our collection of curated accessories. AC Wall Plug Adapter Recall Program Apple Footer Shop and Learn Open Menu Close Menu MaciPadiPhoneWatchTVMusiciTunesiPodAccessoriesGift Cards Apple Store Open Menu Close Menu Find a StoreGenius BarWorkshops and LearningYouth ProgramsApple Store AppRefurbishedFinancingReuse and RecyclingOrder StatusShopping Help For Education Open Menu Close Menu Apple and EducationShop for College For Business Open Menu Close Menu iPhone in BusinessiPad in BusinessMac in BusinessShop for Your Business Account Open Menu Close Menu Manage Your Apple IDApple Store AccountiCloud.com Apple Values Open Menu Close Menu EnvironmentSupplier ResponsibilityAccessibilityPrivacyInclusion and DiversityEducation About Apple Open Menu Close Menu Apple InfoJob OpportunitiesPress InfoInvestorsEventsHot NewsContact Apple More ways to shop: Visit an Apple Store, call 1-800-MY-APPLE, or find a reseller. United States Copyright Â© 2016 Apple Inc. All rights reserved. Privacy Policy Terms of Use Sales and Refunds Legal Site Map</content>
  	</page>
  </crawledData>
  ```

<p align="center">
  <img src="https://github.com/wasiahmad/AOL-Query-Log-Analysis/blob/master/results/person-mention.png" width="750"/>
</p>
