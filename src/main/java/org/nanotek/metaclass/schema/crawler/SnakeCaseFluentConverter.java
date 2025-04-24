package org.nanotek.metaclass.schema.crawler;

/**
 * A 'Better of Worlds' snake_case camel case converter
 * 
 */

public record SnakeCaseFluentConverter(){
    public SnakeCaseFluentConverter(){}

    public static String from (String snakeCase){
        return snakeCase.chars()
        .mapToObj(c -> String.valueOf( (char)c))
        .map(s -> 
        
            BooleanStringPair.from(
                s.equals("_")?false:true,
                s
            )
        
        ).reduce((a,b)-> computeRedux(a,b)).get().right();
        
    }

    private static BooleanStringPair computeRedux(BooleanStringPair c , BooleanStringPair b){

        return BooleanStringPair.from(
            
                b.left() , 
                c.left() == false && b.left() == true?
                c.right().concat(b.left()==true?b.right().toUpperCase():""):
                c.left() == true && b.left()==false ?  c.right(): c.right().concat(b.right())

        
        );
    }
    record BooleanStringPair(Boolean left , String  right)
    implements DistinctMemberPair<Boolean ,String > {

        public static BooleanStringPair from(Boolean left , String right){
            return new BooleanStringPair(left , right);
        }

    }
    
    interface DistinctMemberPair<L,R> {

        L left();
        R right();

    }
    
}