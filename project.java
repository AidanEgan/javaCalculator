import java.util.*; 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Simple Calculator Version 2.0

public class Project {
    public static void main(String[] args){
    	System.out.println("Type 'help' for help, and 'exit' to exit. Otherwise input a math problem.");
    	Project p = new Project();
        p.input();
    }
    
    public void input(){
    	Double ans = 0.0;
    	while(true){
    		String str;
    		System.out.print("> ");
    		Scanner scan = new Scanner(System.in);
    		str = scan.nextLine();
    		str = properFormatting(str, ans);
    		if (str.matches("help")){
    			System.out.println("Please type a math problem. Be sure to use '*' not 'x'. 'ans' is the previous answer.");
    			continue;
    		}
    		if (str.matches("exit")){
    			System.out.println("Program Ended");
    			scan.close();
    			break;
    		}
    		System.out.println(findNumbers(str) + " : " + findOpperands(str));
    		ans = parenthesisFinder(findNumbers(str), findOpperands(str));
    		System.out.println(ans);
    	}
    }
    
    //STRING FORMATTTING
    
    public String properFormatting(String s, Double a){
    	s = s.replaceAll(" ", "").toLowerCase();
		if (s.endsWith(".")){
			s = s.substring(0, s.length()-1);
		}
		s = s.replaceAll("ans", a.toString());
		s = s.replaceAll("!\\)", "!+0\\)");
		List<String> tempStr = new ArrayList<>();
		Matcher m = Pattern.compile("[^\\d\\w)](?=[+ \\- * \\/ % ^])-\\d+\\.?\\d*").matcher(s);
   	 		while (m.find()) {
   	 			tempStr.add(m.group());
   	 		}
   	 	if (s.startsWith("-")){
   	 		Matcher match = Pattern.compile("-\\d+\\.?\\d*").matcher(s);
   	 		if (match.find()) {tempStr.add("&"+match.group(0));}
   	 	}
   	 	for (int y =0; y < tempStr.size(); y++){
   	 		String replacer = tempStr.get(y).substring(1,tempStr.get(y).length());
   	 		replacer = "(" + "0" + replacer.substring(0,replacer.length()) + ")";
   	 		s = s.replace(tempStr.get(y).substring(1,tempStr.get(y).length()), replacer);
   	 	}
   	 	boolean replaced = false;
   	 	while (replaced == false){
			if (s.contains("))")){
				s = s.replaceAll("\\)\\)", "\\)+0\\)");
				continue;
			}
			if (s.contains("((")){
				s = s.replaceAll("\\(\\(", "\\(0+\\(");
				continue;
			}
			replaced = true;
		}
    	return s;
    }
    
    //FIND NUMERS AND OPERANDS
    
    public List<String> findNumbers(String str){
    	//Find the numbers that you need
    	List<String> withParenthesis = new ArrayList<String>();
    	Matcher m = Pattern.compile("\\(?\\d+\\.?\\d*\\)?").matcher(str);
    	 while (m.find()) {
    		 withParenthesis.add(m.group());
    	 }
    	 return withParenthesis;
    }
    
    public List<String> findOpperands(String str){
    	String[] opperands = {"!","-","+","*","/","%","^"};
    	String [] strArray = str.split("\\(?\\d+\\.?\\d*\\)?");
    	List<String> returned = new ArrayList<String>();
		for (int x = 0; x < strArray.length; x++){
			for (int y = 0; y < opperands.length; y++){
				if (strArray[x].contains(opperands[y])){
					returned.add(opperands[y]);
				}
			}
		}
		return returned;
    }
    
    //DEAL WITH PARENTHESIS
    
    public Double parenthesisFinder(List<String> nums, List<String> opps){
    	int start = -1;
    	int stop = -1;
    	int skips = 0;
    	for (int i = 0; i < nums.size(); i++){
    		if (nums.get(i).contains("(") && start == -1){
    			nums.add(i, nums.get(i).replaceAll("\\(", ""));
    			nums.remove(i+1);
    			start = i;
    		}
    		if (nums.get(i).contains("(") && start != -1){
    			skips++;
    		}
    		if (nums.get(i).contains(")") && skips == 0){
    			stop = i;
    			nums.add(i, nums.get(i).replaceAll("\\)", ""));
        		nums.remove(i+1);
    		} else if (nums.get(i).contains(")") && skips != 0) { skips--; }
    	}
    	if (start == -1 && stop == -1){
    		//no parenthesis were found
    		List<Double> numsAsDubs = new ArrayList<Double>();
    		for (String s : nums) {
    		    numsAsDubs.add(Double.parseDouble(s));
    		}
    		return math(numsAsDubs,opps);
    	} else {
    		
    		//parenthesis were found
    		try{
    			List<String> tempNumList = new ArrayList<>(nums.subList(start, stop+1));
    			List<String> tempOppList = new ArrayList<>(opps.subList(start, stop));
    			tempNumList = null;
    			tempOppList = null;
    		} catch(IllegalArgumentException e){
    			System.out.println("Parentheis get lonely without a match. Make sure all of yours have one.");
    			return 0.0;
    		}finally{}
    		List<String> tempNumList = new ArrayList<>(nums.subList(start, stop+1));
			List<String> tempOppList = new ArrayList<>(opps.subList(start, stop));
    		for (int x = start; x < stop; x++){
    			nums.remove(start);
    			opps.remove(start);
    		}
    		nums.remove(start);
    		
    		Double replacement = parenthesisFinder(tempNumList,tempOppList);
    		//System.out.println("replacement" + replacement);
    		nums.add(start,replacement.toString());
    		return parenthesisFinder(nums, opps);
    	}
    }
    
    //DO MATH ON THE INDEXES OF THE LISTS THAT ARE PASSED
    
    public Double math(List<Double> numbers, List<String> opperands){
    	Double temp = 0.0;
    	for (int x = 0; x < opperands.size(); x++){
    		if (opperands.get(x) == "!"){
    			temp = fact(numbers.get(x));
    			numbers.remove(x);
    			numbers.add(x, temp);
    			opperands.remove(x);
    		}
    	}
    	if (numbers.size() == opperands.size()){
    		System.out.println("This isn't right");
    		return 0.0;
    	}
    	while (opperands.size() > 0){
    		for (int a = 0; a < opperands.size(); a++){
    			temp = null;
    			if (opperands.get(a) == "^"){
    				temp = Math.pow(numbers.get(a),numbers.get(a+1));
    			}
    			if (temp != null){
    				numbers.remove(a);
    				numbers.remove(a);
    				numbers.add(a, temp);
    				opperands.remove(a);
    			}
    		}
    		for (int b = 0; b < opperands.size(); b++){
    			temp = null;
    			if (opperands.get(b) == "%"){
    				temp = numbers.get(b)%numbers.get(b+1);
    			} 
    			if (temp != null){
    				numbers.remove(b);
    				numbers.remove(b);
    				numbers.add(b, temp);
    				opperands.remove(b);
    			}
    		}
    		for (int c = 0; c < opperands.size(); c++){
    			temp = null;
    			if (opperands.get(c) == "/" || opperands.get(c) == "*"){
    				if (opperands.get(c) == "/"){
    					temp = numbers.get(c)/numbers.get(c+1);
    				} else {
    					temp = numbers.get(c)*numbers.get(c+1);
    				}
    			} 
    			if (temp != null){
    				numbers.remove(c);
    				numbers.remove(c);
    				numbers.add(c, temp);
    				opperands.remove(c);
    			}
    		}
    		for (int d = 0; d < opperands.size(); d++){
    			temp = null;
    			if(opperands.get(d) == "+" || opperands.get(d) == "-"){
    				if (opperands.get(d) == "+"){
    					temp = numbers.get(d)+numbers.get(d+1);
    				} else {
    					temp = numbers.get(d)-numbers.get(d+1);
    				}
    			}
    			if (temp != null){
    				numbers.remove(d);
    				numbers.remove(d);
    				numbers.add(d, temp);
    				opperands.remove(d);
    			}
    		}
    	}
    	return numbers.get(0);
    }
    
    //COMPUTE FACTORIALS
    
    public static double fact(double n){
		if (n <= 1) 
			return 1;
		else 
			return n * fact(n-1);
	}
}
