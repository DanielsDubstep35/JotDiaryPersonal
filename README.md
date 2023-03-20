# JotDiary-1 

## CONTRIBUTING. 
### How to contribute
1. Fork this repo
2. Clone your forked version of this repo
3. Create a branch which somewhat describes the feature you are working on. Example: `add-image`, `fix-network-issue`, etc
```
git checkout -b <branch-name-here>
```
4. Make needed changes and commit with a sensible commit message and push to your fork
```
git add .
git commit -m "<commit-msg-here>"
git push --set-upstream origin <branch-name-here>
```
5. Create a Pull Request
6. Keep patience for the project maintainer to review and merge the PR  
  
## REVIEW. 
### Testing the PR in your own environment. 

```
gh pr checkout PULLREQUEST
```
To check out a pull request locally, use the gh pr checkout subcommand. Replace pull-request with the number, URL, or head branch of the pull request.  
  
Under your repository name, click  Issues or  Pull requests. Issues and pull requests tab selection.
In the "Pull Requests" list, click the pull request you'd like to merge.
Find the ID number of the inactive pull request. This is the sequence of digits right after the pull request's title. Eg (gh pr checkout 1).  
  
* Make an local environment for testing PR.
* Move to environment in terminal.
* gh pr checkout PULL-REQUEST. 
* Run code in your environemnt.  
  
## CODING CONVENTIONS. 
### Naming conventions. 
* Use semantic that is simple and understandable.  
* var is lower-case const is upper-case. 

```kontlin
val dynamic_variable = 0;
const val STATIC_VARIABLE = 9.81;
```
* Models should be consistent with design documentations.  
* Comment code when needed.  

### Documentation.  
Coding should be done in syntax that can be transformed into java-doc. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html#examples.  

Example:  

  
```java
/**
* Class Calculator: (explane the class/file)
* Calculator holds to primitive data, x and y.
* The calculator can do basic operations on then. add, subtract, multiply, divide, etc...
**/
public class Calculator {

/**
* This is a Constructor.
* Makes an instance of the object type Calculator.
* has two atrributes with default value of x = 0 and y = 0.
**/
public Calculator{
x = 0;
y = 0;
}

/**
* This method has two parameters, x and y. It return sum of the two parameters.
*
* @param x - First number.
* @param y - Second number.
* @return - The sum from x and y.
**/
public int add(x,y){
return x + y;
}

}
```
