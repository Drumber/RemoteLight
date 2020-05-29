num = strip.ledNumber()
c = {}
red = 255
while true do
	if strip.delayReached() then
		red = red - 1
		if red <= 0 then
			red = 255
		end
		strip.setAll({red,0,0})
	end
end